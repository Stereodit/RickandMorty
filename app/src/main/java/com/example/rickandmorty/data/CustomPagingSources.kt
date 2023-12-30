package com.example.rickandmorty.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.models.Episode
import com.example.rickandmorty.data.models.Location
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import retrofit2.HttpException

class CharactersPagingSource(
    private val apiSource: RickAndMortyApiService
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val pageIndex = params.key ?: 1
            val response = apiSource.getCharactersByPage(pageIndex)

            return LoadResult.Page(
                data = response.results,
                prevKey = if (response.info.prev == null) null else pageIndex - 1,
                nextKey = if (response.info.next == null) null else pageIndex + 1,
            )
        } catch (e: HttpException) {
            if (e.code() == 404)
                LoadResult.Page(emptyList(), null, null)
            else LoadResult.Error(throwable = e)
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int?  = 1
}

//class CharactersFilteredPagingSource(
//    private val apiSource: RickAndMortyApiService,
//    private val searchByName: String,
//    private val searchByStatus: String,
//    private val searchBySpecies: String,
//    private val searchByGender: String,
//) : PagingSource<Int, Character>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
//        return try {
//            val pageIndex = params.key ?: 1
//            val response = apiSource.getFilteredCharactersByPage(pageIndex, searchByName, searchByStatus, searchBySpecies, searchByGender)
//
////            delay(2_000)
//
//            return LoadResult.Page(
//                data = response.results,
//                prevKey = if (response.info.prev == null) null else pageIndex - 1,
//                nextKey = if (response.info.next == null) null else pageIndex + 1,
//            )
//        } catch (e: HttpException) {
//            if (e.code() == 404)
//                LoadResult.Page(emptyList(), null, null)
//            else LoadResult.Error(throwable = e)
//        } catch (e: Exception) {
//            LoadResult.Error(throwable = e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Character>): Int?  = 1
//}

class EpisodesPagingSource(
    private val apiSource: RickAndMortyApiService,
) : PagingSource<Int, Episode>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Episode> {
        return try {
            val pageIndex = params.key ?: 1
            val response = apiSource.getEpisodesByPage(pageIndex)

//            delay(2_000)

            return LoadResult.Page(
                data = response.results,
                prevKey = if (response.info.prev == null) null else pageIndex - 1,
                nextKey = if (response.info.next == null) null else pageIndex + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Episode>): Int?  = 1
}

class LocationsPagingSource(
    private val apiSource: RickAndMortyApiService,
) : PagingSource<Int, Location>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Location> {
        return try {
            val pageIndex = params.key ?: 1
            val response = apiSource.getLocationsByPage(pageIndex)

//            delay(2_000)

            return LoadResult.Page(
                data = response.results,
                prevKey = if (response.info.prev == null) null else pageIndex - 1,
                nextKey = if (response.info.next == null) null else pageIndex + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Location>): Int?  = 1
}