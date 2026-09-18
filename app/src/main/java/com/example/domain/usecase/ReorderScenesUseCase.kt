package com.example.domain.usecase

import com.example.domain.model.Scene
import javax.inject.Inject

class ReorderScenesUseCase @Inject constructor() {

    operator fun invoke(scenes: List<Scene>, fromIndex: Int, toIndex: Int): List<Scene> {
        if (fromIndex !in scenes.indices || toIndex !in scenes.indices || fromIndex == toIndex) {
            return scenes
        }

        val mutableList = scenes.toMutableList()
        val item = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, item)

        return mutableList.mapIndexed { index, scene ->
            scene.copy(position = index)
        }
    }
}
