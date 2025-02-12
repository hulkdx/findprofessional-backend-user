package com.hulkdx.findprofessional.service

import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.UserUpdateRequest
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.utils.toNormalUserResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    suspend fun updateUser(userId: Long, body: UserUpdateRequest): UserResponse? {
        val user = userRepository.findById(userId) ?: return null
        var updatedUser = user
        if (body.firstName != null) {
            updatedUser = updatedUser.copy(firstName = body.firstName)
        }
        if (body.lastName != null) {
            updatedUser = updatedUser.copy(lastName = body.lastName)
        }
        if (body.profileImage != null) {
            updatedUser = updatedUser.copy(profileImage = body.profileImage)
        }
        if (body.skypeId != null) {
            updatedUser = updatedUser.copy(skypeId = body.skypeId)
        }
        userRepository.save(updatedUser)
        return updatedUser.toNormalUserResponse()
    }
}
