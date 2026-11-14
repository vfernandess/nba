package com.voidx.nba.feature.player

import com.voidx.nba.feature.player.business.SearchPlayersUseCase
import com.voidx.nba.feature.player.presentation.PlayerInteractor
import com.voidx.nba.feature.player.presentation.PlayerReducer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class PlayerModule {

    @Binds
    abstract fun bindsPlayerInteractor(interactor: PlayerInteractor.Impl): PlayerInteractor

    @Binds
    abstract fun bindsPlayerReducer(reducer: PlayerReducer.Impl): PlayerReducer

    @Binds
    abstract fun bindsSearchPlayersUseCase(useCase: SearchPlayersUseCase.Impl): SearchPlayersUseCase
}
