package com.pumpkin.core.math

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

fun transform(position: Vec3, rotation: Vec3, scale: Vec3): Mat4 =
    glm.translate(Mat4.identity, position) *
    glm.rotateXYZ(Mat4.identity, rotation.x, rotation.y, rotation.z) *
    glm.scale(Mat4.identity, scale)

fun transform(t: FloatArray): Mat4 =
    glm.translate(Mat4.identity, t[0], t[1], t[2]) *
            glm.rotateXYZ(Mat4.identity, t[3], t[4], t[5]) *
            glm.scale(Mat4.identity, t[6], t[7], t[8])

fun inverseTransform(position: Vec3, rotation: Vec3, scale: Vec3): Mat4 =
    glm.scale(Mat4.identity, inverseScale(scale)) *
    glm.rotateXYZ(Mat4.identity, -(rotation.x), -(rotation.y), -(rotation.z)) *
    glm.translate(Mat4.identity, -position)

fun inverseTransform(t: FloatArray): Mat4 =
    glm.scale(Mat4.identity, 1 / t[6], 1 / t[7], 1 / t[8]) *
            glm.rotateXYZ(Mat4.identity, -t[3], -t[4], -t[5]) *
            glm.translate(Mat4.identity, -t[0], -t[1], -t[2])

fun inverseScale(scale: Vec3): Vec3 = Vec3(1 / scale.x, 1 / scale.y, 1 / scale.z)