# Project context

This repository is a brownfield Java project used in an undergraduate software engineering course. Students work in teams to extend and refactor an existing application built on the SE-EDU AddressBook-Level3 (AB3) codebase.

Read [README.md](README.md) for the product context and direction. Documentation may include inherited examples or proposed designs; check the relevant code and tests before describing a feature as implemented or planned.

Keep this file focused on durable working practices and explicit environment requirements. Refer to the current documentation and configuration for product details and build commands instead of duplicating them here.

## Working in an existing codebase

* Trace the relevant behavior and read nearby tests before making changes. Extend the existing design and keep changes focused on the requested task.
* Preserve existing behavior, command syntax, and stored data compatibility unless the task calls for changing them. Explain any necessary compatibility changes.
* Follow the component boundaries and interfaces established by the current code. Consult the [Developer Guide](docs/DeveloperGuide.md) for design context.
* Use the names and package structure in the current code. When a naming refactor is in scope, update affected code, resources, configuration, tests, and documentation consistently.
* Update the relevant tests and documentation when changing behavior. Distinguish implemented behavior from proposed features and inherited AB3 examples.

## Repository orientation

* `src/main/java/`: application code; inspect the current package structure for components and entry points.
* `src/main/resources/`: runtime resources, including UI layouts and styles.
* `src/test/java/`: tests and shared test helpers; follow the current package layout and testing conventions.
* `src/test/data/` and `src/test/resources/`: test fixtures and resources.
* `docs/`: User Guide, Developer Guide, setup and testing instructions, and PlantUML sources in `docs/diagrams/`.
* `build.gradle`, `config/`, and `.github/workflows/`: build configuration, coding checks, and CI workflows.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working with teammates to evolve this existing application. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Decent
* IDE and level of expertise: Decent

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Build and verification

Use the checked-in Gradle wrapper (`./gradlew` on macOS/Linux or `gradlew.bat` on Windows). See [Setting Up](docs/SettingUp.md) and [Testing](docs/Testing.md) for more details.

Read the build configuration and CI workflows to identify the supported application, test, style, coverage, and repository-format commands. Follow the commands defined there for the relevant checks.

For code changes, run focused tests while developing and the relevant broader checks before finishing. Report any skipped tests or checks that could not run, including environment limitations such as a missing display. Documentation-only changes need formatting and content checks, without an application build.

## Java coding standard

For every task that creates, modifies, refactors, reviews, or generates Java code, you must load and follow the project skill `$seedu-java-coding-standard` at `.agents/skills/seedu-java-coding-standard/SKILL.md`. Its rules are mandatory for all production and test Java code in this repository.

## Git

For every task that proposes, reviews, creates, or amends a commit or commit message, you must load and follow the project skill `$seedu-git-standard` at `.agents/skills/seedu-git-standard/SKILL.md`. Its rules are mandatory for all future commits in this repository. Use the same skill when naming or creating branches.
