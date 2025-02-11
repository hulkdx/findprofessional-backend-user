package com.hulkdx.findprofessional.service

import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.utils.toNormalUserResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    suspend fun updateUser(userId: Long, body: RegisterRequest): UserResponse? {
        val user = userRepository.findById(userId) ?: return null
        val updatedUser = user.copy(
            email = body.email,
            firstName = body.firstName,
            lastName = body.lastName,
            profileImage = body.profileImage,
            skypeId = body.skypeId,
        )
        userRepository.save(updatedUser)
        return updatedUser.toNormalUserResponse()
    }
}
