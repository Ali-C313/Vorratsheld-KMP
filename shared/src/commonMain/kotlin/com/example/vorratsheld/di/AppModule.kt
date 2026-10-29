package com.example.vorratsheld.di

import com.example.vorratsheld.data.remote.EmailJsApi
import com.example.vorratsheld.data.remote.OpenFoodFactsApi
import com.example.vorratsheld.data.repository.AuthRepository
import com.example.vorratsheld.data.repository.AuthRepositoryImpl
import com.example.vorratsheld.data.repository.OpenFoodFactsRepository
import com.example.vorratsheld.data.repository.OpenFoodFactsRepositoryImpl
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.data.repository.ProductRepositoryImpl
import com.example.vorratsheld.data.repository.ShoppingRepository
import com.example.vorratsheld.data.repository.ShoppingRepositoryImpl
import com.example.vorratsheld.data.repository.SupportRepository
import com.example.vorratsheld.data.repository.SupportRepositoryImpl
import com.example.vorratsheld.data.repository.UserRepository
import com.example.vorratsheld.data.repository.UserRepositoryImpl
import com.example.vorratsheld.data.settings.AppSettings
import com.example.vorratsheld.ui.locale.customAppLocale
import com.example.vorratsheld.ui.applock.AppLockViewModel
import com.example.vorratsheld.ui.auth.AuthViewModel
import com.example.vorratsheld.ui.productdetail.ProductDetailViewModel
import com.example.vorratsheld.ui.productinfo.ProductInfoViewModel
import com.example.vorratsheld.ui.overview.ProductViewModel
import com.example.vorratsheld.ui.editprofile.ProfileViewModel
import com.example.vorratsheld.ui.shoppinglist.ShoppingListViewModel
import com.example.vorratsheld.ui.help.SupportViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Alle Abhängigkeiten der App, Repositories werden als Interface eingetragen
val appModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }

    // Open Food Facts schickt viel mehr Felder als gebraucht
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    singleOf(::OpenFoodFactsApi)
    singleOf(::EmailJsApi)

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<ShoppingRepository> { ShoppingRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<SupportRepository> { SupportRepositoryImpl(get(), get()) }
    single<OpenFoodFactsRepository> { OpenFoodFactsRepositoryImpl(get()) }

    viewModelOf(::AuthViewModel)
    viewModelOf(::ProductViewModel)
    viewModelOf(::ProductDetailViewModel)
    viewModelOf(::ProductInfoViewModel)
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SupportViewModel)
    viewModelOf(::AppLockViewModel)
}

// Wird einmal beim Start aufgerufen: Android in VorratsheldApp, iOS in iOSApp.swift
fun initKoin() {
    startKoin {
        modules(appModule)
    }
    // Gespeicherte Sprache schon vor dem ersten Screen setzen
    customAppLocale = AppSettings.language
}
