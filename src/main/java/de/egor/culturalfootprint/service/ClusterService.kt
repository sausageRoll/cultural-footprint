package de.egor.culturalfootprint.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.ClusterStatus
import de.egor.culturalfootprint.repository.ClusterRepository
import de.egor.culturalfootprint.repository.RawRecordRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ClusterService(
    private val clusterRepository: ClusterRepository,
    private val rawRecordRepository: RawRecordRepository
) {

    suspend fun findCluster(week: String?): List<Cluster> =
        week?.let { clusterRepository.findClustersByWeek(week) }
            ?: clusterRepository.findClusters()

    suspend fun findApprovedDataFor(clusterId: UUID): ClusterResult? =
        clusterRepository.findClusterById(clusterId)
            ?.let {
                ClusterResult(
                    cluster = it,
                    records = rawRecordRepository.findAllByClusterIdAndApproved(clusterId)
                )
            }

    suspend fun findById(clusterId: UUID): Cluster? = clusterRepository.findClusterById(clusterId)

    suspend fun findClusterById(clusterId: UUID): ClusterResult? =
        clusterRepository.findClusterById(clusterId)
            ?.let {
                ClusterResult(
                    cluster = it,
                    records = rawRecordRepository.findAllByClusterId(clusterId)
                )
            }

    suspend fun submitApproval(clusterId: UUID): Boolean =
        clusterRepository.updateStatus(clusterId, ClusterStatus.APPROVED)

    suspend fun submitDeclination(clusterId: UUID): Boolean =
        clusterRepository.updateStatus(clusterId, ClusterStatus.DECLINED)

    suspend fun updateName(clusterId: UUID, name: String) =
        clusterRepository.updateName(clusterId, name)

    suspend fun publish(clusterId: UUID): Boolean = clusterRepository.makePublished(clusterId)

    suspend fun likedBy(clusterId: UUID, userEntity: UserEntity) =
        clusterRepository.likedBy(clusterId, userEntity)

    suspend fun dislikedBy(clusterId: UUID, userEntity: UserEntity) =
        clusterRepository.dislikedBy(clusterId, userEntity)

    suspend fun updateTelegramPostId(clusterId: UUID, messageId: Int)
        = clusterRepository.updateTelegramPostId(clusterId, messageId)
}
