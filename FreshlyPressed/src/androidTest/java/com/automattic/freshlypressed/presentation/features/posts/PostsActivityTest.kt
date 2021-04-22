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

package com.automattic.freshlypressed.presentation.features.posts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.automattic.freshlypressed.R
import com.m2f.domain.di.EndpointModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@LargeTest
@HiltAndroidTest
@UninstallModules(EndpointModule::class)
class PostsActivityTest {

    private val activityRule = ActivityTestRule(PostsActivity::class.java, true, false)

    @get:Rule
    var rule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this))
        .around(activityRule)

    @Test
    fun postsActivityTest() {
        runBlocking {

            activityRule.launchActivity(null)
            val recyclerView = onView(
                allOf(
                    withId(R.id.list),
                    withParent(
                        allOf(
                            withId(R.id.content),
                            withParent(withId(R.id.list))
                        )
                    ),
                    isDisplayed()
                )
            )
            recyclerView.check(matches(isDisplayed()))

        }
    }
}
