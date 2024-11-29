package com.hackernewschallenge.data.model

import com.hackernewschallenge.domain.models.Hits

data class NewsResponse(
    val hits: List<Hits>?
)