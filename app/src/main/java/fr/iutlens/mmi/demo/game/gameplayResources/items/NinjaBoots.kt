package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class NinjaBoots : Item(
    image = R.drawable.sandal,
    name = "Sandales de Ninja",
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque pulvinar tempor neque at eleifend. Nam odio nunc, placerat volutpat tempor non, iaculis nec lectus. Curabitur in nulla fermentum, aliquam velit eu, mattis ipsum. Suspendisse eget lectus ex. Nulla sed nisl consequat, sodales nulla eget, blandit nulla. Nunc eget rutrum est, nec euismod leo. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque pulvinar tempor neque at eleifend. Nam odio nunc, placerat volutpat tempor non, iaculis nec lectus. Curabitur in nulla fermentum, aliquam velit eu, mattis ipsum. Suspendisse eget lectus ex. Nulla sed nisl consequat, sodales nulla eget, blandit nulla. Nunc eget rutrum est, nec euismod leo.",
    effects = {
        game -> game.controllableCharacter!!.speed+=0.05f
    }
) {
}