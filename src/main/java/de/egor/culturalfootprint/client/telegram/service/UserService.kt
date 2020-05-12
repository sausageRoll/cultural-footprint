package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.types.Message
import de.egor.culturalfootprint.client.telegram.converter.Converter
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.client.telegram.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val telegramToModelConverter: Converter<Message, UserEntity>
) {

    fun findAll() = userRepository.findAll()

    suspend fun handleUser(user: Message) =
        user.let { telegramToModelConverter.convert(it) }
            .let { userRepository.updateOrInsert(it) }

}
