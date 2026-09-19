package com.example.magipath

data class PlanTask(
    val exerciseId: String,
    val label: String,
    val minutes: Int
)

data class PlanStage(
    val title: String,
    val description: String,
    val tasks: List<PlanTask>
)

val planStages = listOf(
    PlanStage(
        "Основа",
        "Учимся расслабляться и правильно дышать. Внимание — телу и дыханию.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("relax", "Этап 1", 5),
            PlanTask("breath", "Основа", 5)
        )
    ),
    PlanStage(
        "Ритм и тишина",
        "Замедляем ритм тела и начинаем останавливать внутреннюю болтовню.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("relax", "Этапы 1+2", 10),
            PlanTask("breath", "Ритм", 5),
            PlanTask("silence", "Переключение", 5)
        )
    ),
    PlanStage(
        "Тело и первые образы",
        "Учимся отчуждению тела и делаем первые шаги в визуализации.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("relax", "Все 3 этапа", 12),
            PlanTask("silence", "Угасание, Фильтр", 8),
            PlanTask("anchor", "Точка, Линия", 5)
        )
    ),
    PlanStage(
        "Фигуры и удержание",
        "Удерживаем простые фигуры и учимся неподвижности.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("silence", "Перегрузка", 5),
            PlanTask("anchor", "Круг, Квадрат", 10),
            PlanTask("focus", "Неподвижность", 5)
        )
    ),
    PlanStage(
        "Объём и фокус",
        "Куб, радужный счёт и остановка вращения.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("anchor", "Куб", 5),
            PlanTask("focus", "Радужный счёт, Остановка", 12),
            PlanTask("gaze", "Точка", 5)
        )
    ),
    PlanStage(
        "Углубление",
        "Сложные визуализации, взгляд и продолжение фокуса.",
        listOf(
            PlanTask("setup", "Полный цикл", 5),
            PlanTask("focus", "Круг цветов, Мерность", 15),
            PlanTask("gaze", "Зайчик, Перевод", 10)
        )
    )
)
