package de.egor.culturalfootprint.client.telegram.converter

import com.elbekD.bot.types.Message
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TelegramToModelConverter : Converter<Message, UserEntity> {
    override fun convert(source: Message): UserEntity =
        UserEntity(
            id = UUID.randomUUID(),
            chatId = source.chat.id,
            firstName = source.from?.first_name,
            lastName = source.from?.last_name,
            username = source.from?.username
        )

}
