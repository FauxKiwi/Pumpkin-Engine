import com.pumpkin.core.scene.Scene
import com.pumpkin.core.scene.SpriteRendererComponent
import com.pumpkin.core.scene.TransformComponent

fun main() {
    val scene = Scene()
    val entity = scene.createEntity()
    entity.addComponent(TransformComponent(floatArrayOf(1f, -1f, 0f, 5f, 2f, 0f)))
    val transform = entity.getComponent<TransformComponent>()
    println("Position: ${transform.position} Scale: ${transform.scale} Rotation: ${transform.rotation}")
    entity.addComponent(SpriteRendererComponent(floatArrayOf(1f, 0f, 1f, 1f)))
    val sprite = entity.getComponent<SpriteRendererComponent>()
    println("Color: ${sprite.color}")
    /*val view = scene.registry.view<TransformComponent>()
    for (e in view) {
        val t = view.get(e)
        println("$e: Position: ${t.position} Scale: ${t.scale} Rotation: ${t.rotation}")
    }*/
    val group = scene.registry.group<TransformComponent, SpriteRendererComponent>()
    for (e in group) {
        val (t, s) = group.get(e)
        println("$e: Position: ${t.position} Scale: ${t.scale} Rotation: ${t.rotation} Color: ${s.color}")
    }
}