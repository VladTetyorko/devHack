# ModelBuilder

## Overview

The `ModelBuilder` class is a generic builder for constructing Spring MVC models. It provides a fluent API for adding attributes to a model using lambda functions, following the builder pattern to allow chaining of method calls.

## Purpose

The ModelBuilder was created to replace different view and response services with a generic solution that uses lambda functions. This approach:

1. Reduces code duplication
2. Provides a consistent way to build models
3. Makes the code more readable and maintainable
4. Allows for more flexible model building

## Usage

### Basic Usage

```java
// Create a ModelBuilder for a model
ModelBuilder.of(model)
    .addAttribute("key", value)
    .setPageTitle("Page Title")
    .build();
```

### Adding Single Entities

```java
ModelBuilder.of(model)
    .addSingleEntity(entity, "entityName")
    .build();
```

### Working with Answers

```java
ModelBuilder.addSingleAnswer(model, answerId)
    .addQuestions()
    .build();
```

### Adding Pagination Data

```java
ModelBuilder.of(model)
    .addPagination(page, currentPage, size, "items")
    .build();
```

### Conditional Logic

```java
ModelBuilder builder = ModelBuilder.of(model);

if (condition) {
    builder.addAttribute("conditionalKey", conditionalValue);
}

builder.addAttribute("key", value)
       .build();
```

### Transforming Entities

```java
ModelBuilder.of(model)
    .transformAndAdd(entity, transformer, "transformedEntity")
    .build();
```

### Using Consumer Functions

```java
ModelBuilder.of(model)
    .apply(m -> {
        // Custom logic to modify the model
        m.addAttribute("customKey", customValue);
    })
    .build();
```

## Available Methods

- `of(Model model)`: Create a new ModelBuilder for the given model
- `addAttribute(String name, Object value)`: Add an attribute to the model
- `setPageTitle(String title)`: Set the page title in the model
- `apply(Consumer<Model> modelConsumer)`: Apply a consumer function to the model
- `addSingleEntity(T entity, String entityName)`: Add a single entity to the model
- `addSingleAnswer(Model model, Object answerId)`: Add a single answer to the model
- `addQuestions()`: Add questions to the model
- `transformAndAdd(T entity, Function<T, R> transformer, String attributeName)`: Apply a function to transform an entity and add it to the model
- `with(T entity, BiConsumer<Model, T> consumer)`: Apply a bi-consumer function to the model and an entity
- `addPagination(Page<T> page, int currentPage, int size, String contentAttributeName)`: Add pagination data to the model
- `addPagination(Page<T> page, int currentPage, int size)`: Add pagination data with default content attribute name "items"
- `build()`: Build and return the model

## Examples

### Example 1: Simple Model Building

```java
@GetMapping("/{id}/check")
public String checkAnswerWithAi(@PathVariable UUID id, Model model) {
    Answer answer = service.checkAnswerWithAi(id);
    
    ModelBuilder.of(model)
        .addSingleEntity(answer, "answer")
        .addAttribute("message", "Answer checked by AI. Score: " + answer.getAiScore())
        .addAttribute("aiFeedback", answer.getAiFeedback())
        .setPageTitle("Answer Details")
        .build();
        
    return "answers/view";
}
```

### Example 2: Pagination

```java
@GetMapping
public String list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {
    // Get the current authenticated user
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();

    // Find the user by email
    User currentUser = userService.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalStateException("Current user not found"));

    // Create pageable object
    Pageable pageable = PageRequest.of(page, size);

    // Get answers for the current user with pagination
    Page<Answer> answerPage = service.findAnswersByUser(currentUser, pageable);

    // Using the new ModelBuilder to build the model with pagination data
    ModelBuilder.of(model)
        .addPagination(answerPage, page, size, "answers")
        .setPageTitle("My Answers")
        .build();
        
    return "answers/list";
}
```

### Example 3: Conditional Logic

```java
@GetMapping("/new")
public String newAnswerForm(@RequestParam(required = false) UUID questionId, Model model) {
    Answer answer = new Answer();

    // Create a ModelBuilder
    ModelBuilder builder = ModelBuilder.of(model);
    
    // Add question if provided
    if (questionId != null) {
        Optional<InterviewQuestion> questionOpt = questionService.findById(questionId);
        questionOpt.ifPresent(question -> {
            answer.setQuestion(question);
            builder.addSingleEntity(question, "question");
        });
    }

    // Complete the model building
    builder.addSingleEntity(answer, "answer")
           .addAttribute("users", userService.findAll())
           .addAttribute("questions", questionService.findAll())
           .setPageTitle("Create New Answer")
           .build();
           
    return "answers/form";
}
```

## Migration Guide

To migrate from view services to ModelBuilder:

1. Identify the view service methods that build models
2. Replace them with ModelBuilder calls
3. Use the appropriate ModelBuilder methods to add attributes to the model
4. Chain method calls to build the model in a fluent way
5. Use lambda functions for more complex model building logic

## Benefits

- **Consistency**: All model building follows the same pattern
- **Readability**: The fluent API makes the code more readable
- **Maintainability**: Less code duplication means easier maintenance
- **Flexibility**: Lambda functions allow for more flexible model building
- **Testability**: Easier to test model building logic