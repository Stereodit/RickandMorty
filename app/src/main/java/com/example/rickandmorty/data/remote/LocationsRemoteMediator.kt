package com.example.rickandmorty.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.LocationEntity
import com.example.rickandmorty.data.local.LocationRemoteKeys
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.local.toFullInfoLocation
import com.example.rickandmorty.data.local.toLocationCharactersEntity
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class LocationsRemoteMediator(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
): RemoteMediator<Int, LocationEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (rickAndMortyDatabase.getLocationsRemoteKeysDao.getCreationTime() ?: 0) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult {
        val page: Int = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val apiResponse = rickAndMortyApiService.getLocationsByPage(page = page)
            val locations = apiResponse.results
            val endOfPaginationReached = locations.isEmpty()

            rickAndMortyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    rickAndMortyDatabase.getLocationsRemoteKeysDao.clearRemoteKeys()
                    rickAndMortyDatabase.getLocationsDao.clearLocations()
                    rickAndMortyDatabase.getLocationsDao.clearLocationCharacters()
                }
                val prevKey = if (apiResponse.info.prev == null) null else page - 1
                val nextKey = if (apiResponse.info.next == null) null else page + 1
                val locationRemoteKeys = locations.map {
                    LocationRemoteKeys(locationId = it.id, prevKey = prevKey, currentPage = page, nextKey = nextKey)
                }

                rickAndMortyDatabase.getLocationsRemoteKeysDao.insertAll(locationRemoteKeys)
                rickAndMortyDatabase.getLocationsDao.upsertLocations(locations.map { it.toFullInfoLocation(page).locationEntity })
                locations.forEach {
                    rickAndMortyDatabase.getLocationsDao.upsertLocationCharacters(it.residents.toLocationCharactersEntity(it.id))
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, LocationEntity>): LocationRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                rickAndMortyDatabase.getLocationsRemoteKeysDao.getRemoteKeyByLocationId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, LocationEntity>): LocationRemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { location ->
            rickAndMortyDatabase.getLocationsRemoteKeysDao.getRemoteKeyByLocationId(location.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, LocationEntity>): LocationRemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { location ->
            rickAndMortyDatabase.getLocationsRemoteKeysDao.getRemoteKeyByLocationId(location.id)
        }
    }
}