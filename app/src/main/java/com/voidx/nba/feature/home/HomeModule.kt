package com.voidx.nba.feature.home

import com.voidx.nba.feature.home.business.GetTeamsUseCase
import com.voidx.nba.feature.home.business.SortTeamsUseCase
import com.voidx.nba.feature.home.presentation.HomeInteractor
import com.voidx.nba.feature.home.presentation.HomeReducer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun bindsHomeInteractor(interactor: HomeInteractor.Impl): HomeInteractor

    @Binds
    abstract fun bindsHomeReducer(reducer: HomeReducer.Impl): HomeReducer

    @Binds
    abstract fun bindsGetTeamsUseCase(useCase: GetTeamsUseCase.Impl): GetTeamsUseCase

    @Binds
    abstract fun bindsSortTeamsUseCase(useCase: SortTeamsUseCase.Impl): SortTeamsUseCase
}
