package jp.kaleidot725.adbpad.repository.di

import jp.kaleidot725.adbpad.domain.repository.CommandRepository
import jp.kaleidot725.adbpad.domain.repository.DeviceRepository
import jp.kaleidot725.adbpad.repository.impl.CommandRepositoryImpl
import jp.kaleidot725.adbpad.repository.impl.DeviceRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<DeviceRepository> {
        DeviceRepositoryImpl()
    }
    single<CommandRepository> {
        CommandRepositoryImpl()
    }
}
