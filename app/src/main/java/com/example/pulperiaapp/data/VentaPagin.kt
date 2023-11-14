package com.example.pulperiaapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pulperiaapp.data.Repository.VentaRepositorio
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import kotlinx.coroutines.delay

import javax.inject.Inject


const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L

class VentaPaginSource @Inject constructor(private val ventaRepositorio: VentaRepositorio) :
    PagingSource<Int, VentaPrixCocaDetalle>() {

    override fun getRefreshKey(state: PagingState<Int, VentaPrixCocaDetalle>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val venta = state.closestItemToPosition(anchorPosition) ?: return null

        val retorno = venta.id - state.config.pageSize / 2
        return retorno
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VentaPrixCocaDetalle> {
        return try {

            val start = params.key ?: STARTING_KEY
            if (start != STARTING_KEY) delay(LOAD_DELAY_MILLIS)

            val obtenerVenta = ventaRepositorio.obtenerVenta()
            val ventasPaginadas =
                obtenerVenta.subList(start, minOf(start + params.loadSize))
            val prevkey = if (start == STARTING_KEY) null else start - params.loadSize
            val nextKey =
                if (ventasPaginadas.size < params.loadSize) null else start + params.loadSize

            LoadResult.Page(
                data = ventasPaginadas,
                prevKey = prevkey,
                nextKey = nextKey
            )


        } catch (e: Exception) {
            LoadResult.Error(e)
        }


    }
}