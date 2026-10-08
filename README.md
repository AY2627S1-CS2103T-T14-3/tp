# LinkedOut

[![CI Status](https://github.com/AY2627S1-CS2103T-T14-3/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-T14-3/tp/actions)

![Current contact-management interface](docs/images/Ui.png)

*The current interface comes from the AddressBook foundation; the recruitment workflow is under development.*

**Keep track of the people behind every application.**

LinkedOut is a desktop application in development for recruiters and HR administrators managing student hiring across multiple job openings. It brings applicant contacts, application outcomes, and the people responsible for each role into one place, helping recruiters see where an application stands and whom to contact next.

Designed for people who prefer typing, LinkedOut pairs text commands with a graphical overview. It is a personal workspace for one recruiter, with records stored locally in editable JSON files.

## Project direction

Our initial recruitment workflow focuses on:

- Registering applicants with their contact details and an application to a job opening.
- Recording whether an application is pending, accepted, or rejected.
- Reviewing the applicant list and opening an applicant's full details.
- Showing the relevant job and department contacts alongside an application.

The current code supports adding, editing, finding, listing, and deleting contacts, with local JSON storage. Recruitment-specific commands and views are planned additions.

## Getting started

For project documentation, visit the [LinkedOut product website](https://AY2627S1-CS2103T-T14-3.github.io/tp/).

- [User Guide](docs/UserGuide.md): how to run the application and use its current commands.
- [Developer Guide](docs/DeveloperGuide.md): architecture and implementation details.
- [Setting Up](docs/SettingUp.md): development setup with JDK 25 and Gradle.
- [About Us](docs/AboutUs.md): the team behind LinkedOut.

The guides are being adapted alongside the application and currently describe the AddressBook foundation.

## Acknowledgements

* The remark command follows the [SE-EDU AB3 Adding a Command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html).

LinkedOut uses [JavaFX](https://openjfx.io/) for the interface, [Jackson](https://github.com/FasterXML/jackson) for JSON storage, and [JUnit 5](https://github.com/junit-team/junit5) for testing.

This project is available under the [MIT License](LICENSE).

_This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org)._
