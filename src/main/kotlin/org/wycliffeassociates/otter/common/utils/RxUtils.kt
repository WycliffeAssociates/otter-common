package org.wycliffeassociates.otter.common.utils

import io.reactivex.Observable

fun <T, R:Any> Observable<T>.mapNotNull(f: (T) -> R?): Observable<R> =
    concatMapIterable { listOfNotNull(f(it)) }