/*
 * Copyright (c) 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.m2f.domain.features.posts.data.datasource

class PostsDatabaseDataSourceTest {
//fixme -> for now we are skipping this test as we are not using database due to depenecency problems

/*    private val mockPostDbo = PostDBO(
        id = 0,
        title = "title",
        date = "date",
        excerpt = "exerp",
        url = "url",
        authorAvatarUrl = "https://url",
        authorName = "name",
        authorUrl = "https://authUrl",
        featuredImage = "https://image.png",
        numberOfSubscribers = 0L
    )

    private val nCoroutines = 100
    private val kActions = 1000

    private lateinit var posts: Stack<PostDBO>

    private val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        Database.Schema.create(this)
    }
    private lateinit var postDBOQueries: PostDBOQueries
    private lateinit var databaseDataSource: PostsDatabaseDataSource

    @Before
    fun setUp() {
        postDBOQueries = Database.invoke(driver).postDBOQueries
        databaseDataSource = PostsDatabaseDataSource(postDBOQueries)

        posts = List(nCoroutines * kActions) { mockPostDbo.copy(id = it.toLong()) }.let {
            Stack<PostDBO>().apply {
                addAll(it)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun mutex_willRunAllTheOperationsOnMasiveWrites() = runBlocking {

        withContext(Dispatchers.Default) {
            massiveRun { number ->
                val post = posts.pop()
                //put post one by one using several coroutines
                databaseDataSource.putAll(
                    VoidQuery,
                    List(1) { post })
            }

        }
        val result = databaseDataSource.getAll(PostsQuery(false, 100 * 1000))
        assert(result.fold({ false }, { it.size == 100 * 1000 }))
        assert(posts.empty())

    }

    @Test
    @ExperimentalCoroutinesApi
    fun datasource_willEmitDataNotFoundFailureIfDatabaseIsEmpty() = runBlockingTest {
        //When
        val result = databaseDataSource.getAll(PostsQuery(false, 100 * 1000))

        assertEquals(Either.Left(Failure.DataNotFound), result)

    }
}

/**
 * A function that runes a massive amount of functions in several coroutines
 * @param numberOfActions the number of times we are gona run an action inside a coroutine
 * @param numberOfCoroutines the number of coroutines we are going to launch
 */
suspend fun massiveRun(
    numberOfCoroutines: Int = 100,
    numberOfActions: Int = 1000,
    action: suspend (Int) -> Unit
) {
    coroutineScope { // scope for coroutines
        repeat(numberOfCoroutines) { coroutineN ->
            launch {
                repeat(numberOfActions) { timeK -> action("${coroutineN + 1}$timeK".toInt()) }
            }
        }
    }*/
}