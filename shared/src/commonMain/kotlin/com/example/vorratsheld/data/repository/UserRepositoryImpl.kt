package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.UserProfile
import com.example.vorratsheld.data.remote.CollectionNames
import dev.gitlive.firebase.firestore.FirebaseFirestore

// Profile in der Firestore-Collection "users", Dokument-ID ist die uid
class UserRepositoryImpl(
    firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection(CollectionNames.USERS)

    override suspend fun getProfile(userId: String): Result<UserProfile> = runCatching {
        val snapshot = usersCollection.document(userId).get()
        if (snapshot.exists) snapshot.data<UserProfile>() else UserProfile()
    }

    override suspend fun saveProfile(userId: String, profile: UserProfile): Result<Unit> = runCatching {
        usersCollection.document(userId).set(profile)
    }

    override suspend fun deleteProfile(userId: String): Result<Unit> = runCatching {
        usersCollection.document(userId).delete()
    }
}
