package de.egor.culturalfootprint.client.telegram.callbacks

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.markup.LikeMarkupFactory
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import de.egor.culturalfootprint.client.telegram.service.UserService
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID

class LikeCallbackData(
    val clusterId: UUID,
    userService: UserService,
    clusterService: ClusterService,
    telegramProperties: TelegramProperties,
    likeMarkupFactory: LikeMarkupFactory
) : AbstractLikingCallbackData(clusterService, telegramProperties, likeMarkupFactory, userService) {

    override fun toCallbackString(): String =
        CallbackType.LIKE.prefix + CallbackDataFactory.delimiter + clusterId

    override suspend fun execute(telegramUserId: Int): (Bot) -> Unit {
        val user = findUser(telegramUserId)
        clusterService.likedBy(clusterId, user)
        return updateLikingMarkup(clusterId)
    }

}

class DislikeCallbackData(
    val clusterId: UUID,
    userService: UserService,
    clusterService: ClusterService,
    telegramProperties: TelegramProperties,
    likeMarkupFactory: LikeMarkupFactory
) : AbstractLikingCallbackData(clusterService, telegramProperties, likeMarkupFactory, userService) {

    override fun toCallbackString(): String =
        CallbackType.DISLIKE.prefix + CallbackDataFactory.delimiter + clusterId

    override suspend fun execute(telegramUserId: Int): (Bot) -> Unit {
        val user = findUser(telegramUserId)
        clusterService.dislikedBy(clusterId, user)
        return updateLikingMarkup(clusterId)
    }
}

abstract class AbstractLikingCallbackData(
    val clusterService: ClusterService,
    private val telegramProperties: TelegramProperties,
    private val likeMarkupFactory: LikeMarkupFactory,
    private val userService: UserService
) : CallbackData {

    internal fun updateLikingMarkup(clusterId: UUID): (Bot) -> Unit {
        return { bot ->
            GlobalScope.launch {
                clusterService.findById(clusterId)?.let {
                    bot.editMessageReplyMarkup(
                        chatId = telegramProperties.channelName,
                        messageId = it.telegramPostId,
                        markup = likeMarkupFactory.likingKeyboard(clusterId)
                    ).whenComplete { _, e -> e?.also {
                        log.warn("Exception updating post for cluster {}", clusterId, e)
                    }}
                }
            }
        }
    }

    internal suspend fun findUser(telegramUserId: Int): UserEntity {
        return userService.findByTelegramId(telegramUserId.toLong())
            ?: throw RuntimeException("User not found")
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
