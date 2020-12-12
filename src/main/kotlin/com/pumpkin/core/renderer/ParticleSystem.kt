package com.pumpkin.core.renderer

import com.pumpkin.core.Timestep
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.PI

class ParticleSystem {
    private val random = java.util.Random()

    private var particlePool = Array(1000) { Particle() }
    private var poolIndex = 999
    private var activeParticles = 0

    var maxParticles
        get() = particlePool.size
        set(value) {
            val copy = particlePool.copyOf()
            particlePool = Array(value) { Particle() }
            copy.copyInto(particlePool, value - copy.size)
        }

    fun onUpdate(ts: Timestep) {
        Renderer2D.flushAndReset()
        for (i in 0 until activeParticles) {
            val particle = particlePool[(i+poolIndex) % maxParticles]
            if (!particle.active)
                continue

            particle.lifeRemaining -= (ts * 0.1f)

            if (particle.lifeRemaining <= 0.0f) {
                particle.active = false
                activeParticles--
                continue
            }

            particle.position = particle.position + particle.velocity * ts
            particle.rotation += 0.01f

            val life = particle.lifeRemaining / particle.lifeTime

            val color = lerp(particle.colorEnd, particle.colorBegin, life)

            val size = Vec2(particle.sizeEnd + (particle.sizeBegin - particle.sizeEnd) * life)

            Renderer2D.drawQuad(
                Vec3(particle.position, 0.5f),
                size,
                particle.rotation,
                color
            )
        }
    }

    fun emit(particleProps: ParticleProps): Particle {
        val particle = particlePool[poolIndex]
        if (!particle.active) activeParticles++

        particle.active = true
        particle.position = Vec2(particleProps.position)
        particle.rotation = random.nextFloat() * 2f * PI.toFloat()

        particle.velocity = Vec2(particleProps.velocity)
        particle.velocity.x += particleProps.velocityVariation.x * (random.nextFloat() - 0.5f)
        particle.velocity.y += particleProps.velocityVariation.y * (random.nextFloat() - 0.5f)

        particle.colorBegin = Vec4(particleProps.colorBegin)
        particle.colorEnd = Vec4(particleProps.colorEnd)

        particle.lifeTime = particleProps.lifeTime
        particle.lifeRemaining = particle.lifeTime

        particle.sizeBegin = particleProps.sizeBegin + particleProps.sizeVariation * (random.nextFloat() - 0.5f)
        particle.sizeEnd = particleProps.sizeEnd

        poolIndex--
        if (poolIndex <= 0) poolIndex = particlePool.size - 1

        return particle
    }
}

data class ParticleProps(
    var position: Vec2,
    var velocity: Vec2,
    var velocityVariation: Vec2,
    var colorBegin: Vec4,
    var colorEnd: Vec4,
    var sizeBegin: Float,
    var sizeEnd: Float,
    var sizeVariation: Float,
    var lifeTime: Float = 1.0f,
)

data class Particle(
    var position: Vec2 = Vec2(0),
    var velocity: Vec2 = Vec2(0),
    var colorBegin: Vec4 = Vec4(0),
    var colorEnd: Vec4 = Vec4(0),
    var sizeBegin: Float = 0f,
    var sizeEnd: Float = 0f,
    var lifeTime: Float = 0f,
) {
    var rotation = 0f
    var lifeRemaining = lifeTime
    var active = false
}

fun lerp(v1: Vec4, v2: Vec4, i: Float): Vec4 {
    return Vec4(
        v1.x + (v2.x - v1.x) * i,
        v1.y + (v2.y - v1.y) * i,
        v1.z + (v2.z - v1.z) * i,
        v1.w + (v2.w - v1.w) * i,
    )
}