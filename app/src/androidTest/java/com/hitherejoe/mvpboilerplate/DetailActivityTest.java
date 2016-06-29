package com.hitherejoe.mvpboilerplate;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.apps.secrets.test.common.TestComponentRule;
import com.google.android.apps.secrets.test.common.TestDataFactory;
import com.hitherejoe.mvpboilerplate.data.model.Pokemon;
import com.hitherejoe.mvpboilerplate.data.model.Statistic;
import com.hitherejoe.mvpboilerplate.ui.detail.DetailActivity;
import com.hitherejoe.mvpboilerplate.util.ErrorTestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import rx.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<DetailActivity> main =
            new ActivityTestRule<>(DetailActivity.class, false, false);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public TestRule chain = RuleChain.outerRule(component).around(main);

    @Test
    public void checkPokemonDisplays() {
        Pokemon pokemon = TestDataFactory.makePokemon("id");
        stubDataManagerGetPokemon(Single.just(pokemon));
        main.launchActivity(null);

        onView(withText(pokemon.name))
                .check(matches(isDisplayed()));

        for (Statistic stat : pokemon.stats) {
            onView(withText(stat.stat.name))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void checkErrorViewDisplays() {
        stubDataManagerGetPokemon(Single.<Pokemon>error(new RuntimeException()));
        main.launchActivity(null);
        ErrorTestUtil.checkErrorViewsDisplay();
    }

    @Test
    public void clickingReloadInErrorViewReloadsContent() {
        stubDataManagerGetPokemon(Single.<Pokemon>error(new RuntimeException()));
        main.launchActivity(null);

        Pokemon pokemon = TestDataFactory.makePokemon("id");
        stubDataManagerGetPokemon(Single.just(pokemon));
        ErrorTestUtil.checkClickingReloadShowsContentWithText(pokemon.name);
    }

    public void stubDataManagerGetPokemon(Single<Pokemon> single) {
        when(component.getMockDataManager().getPokemon(anyString()))
                .thenReturn(single);
    }

}