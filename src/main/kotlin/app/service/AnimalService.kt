package app.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import app.exception.CustomException
import app.exception.Type
import app.model.Animal
import app.model.User
import app.repo.AnimalRepository


@Service
class AnimalService(@Autowired private val animalRepository: AnimalRepository) {

    fun create(animal: Animal): Animal {
        if (!animalRepository.existsByNickname(animal.nickname)) {
            return animalRepository.save(animal)
        } else {
            throw CustomException(Type.NICKNAME_ALREADY_IN_USE)
        }
    }

    fun findAll(user: User): List<Animal>? {
        return animalRepository.findByUserId(user.id)
    }

    fun findAndCheck(animalId: Int, user: User): Animal {
        val animal = animalRepository.findByAnimalId(animalId)
        when {
            animal == null -> throw CustomException(Type.NOT_FOUND)
            animal.user!!.id != user.id -> throw CustomException(Type.ANIMAL_NOT_OWNED)
            else -> return animal
        }
    }

    fun delete(animal: Animal) {
        animalRepository.delete(animal)
    }

    fun update(animal: Animal) {
        animalRepository.save(animal)
    }

}
