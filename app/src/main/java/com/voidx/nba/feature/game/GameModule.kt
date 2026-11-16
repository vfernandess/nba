package com.voidx.nba.feature.game

import com.voidx.nba.feature.game.business.CurrentSeason
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import com.voidx.nba.feature.game.business.Today
import com.voidx.nba.feature.game.presentation.GameInteractor
import com.voidx.nba.feature.game.presentation.GameReducer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class GameModule {

    @Binds
    abstract fun bindsGameInteractor(interactor: GameInteractor.Impl): GameInteractor

    @Binds
    abstract fun bindsGameReducer(reducer: GameReducer.Impl): GameReducer

    @Binds
    abstract fun bindsGetTeamGamesUseCase(useCase: GetTeamGamesUseCase.Impl): GetTeamGamesUseCase

    @Binds
    abstract fun bindsCurrentSeason(currentSeason: CurrentSeason.Impl): CurrentSeason

    @Binds
    abstract fun bindsToday(today: Today.Impl): Today
}
