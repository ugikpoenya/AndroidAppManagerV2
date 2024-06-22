package com.ugikpoenya.masterguidev4

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response

class InputStreamVolleyRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<ByteArray>,
    errorListener: Response.ErrorListener? = null
) : Request<ByteArray>(method, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {
        return try {
            Response.success(response.data, null)
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: ByteArray) {
        listener.onResponse(response)
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): MutableMap<String, String> {
        return HashMap()
    }

    override fun getBodyContentType(): String {
        return "application/x-www-form-urlencoded"
    }
}
