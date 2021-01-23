package com.example.coroutinesfun

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FunTest {

    @Test
    fun `deferred not cancelled`() {
        runBlocking {
            var completed = false
            try {
                val job = async {
                    val deferred = CompletableDeferred<String?>()
                    deferred.invokeOnCompletion {
                        completed = true
                    }
                    deferred.await()
                }
                launch { job.cancel() }
                job.await()
            } catch (e: CancellationException) {
            } finally {
                assert(!completed)
            }
        }
    }

    @Test
    fun `deferred cancelled`() {
        runBlocking {
            var completed = false
            try {
                val job = async {
                    val deferred = CompletableDeferred<String?>(coroutineContext[Job])
                    deferred.invokeOnCompletion { throwable ->
                        completed = true
                        assertNotNull(throwable)
                        assert(deferred.isCancelled)
                        assert(throwable is CancellationException)
                    }
                    deferred.await()
                }
                launch { job.cancel() }
                job.await()
            } catch (e: CancellationException) {
            } finally {
                assert(completed)
            }
        }
    }
}