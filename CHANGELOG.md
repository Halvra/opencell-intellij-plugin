<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Opencell Community Tools - IntelliJ Plugin Changelog

## [Unreleased]
### Added
- Shortcut for postman generation on a script (<kbd>Alt</kbd>+<kbd>P</kbd>)
- Action for directly deploy a script to an environment (context menu or <kbd>Alt</kbd>+<kbd>D</kbd>)

### Changed

### Deprecated

### Removed

### Fixed
- Abstract classes extending `org.meveo.service.script.Script` wrongly detected as valid Script

### Security
## [1.0.0]
### Changed
- Plugin name now reflect the real project intention
- Overall code cleanup after [Sonar](https://sonarcloud.io/dashboard?id=opencell-intellij-plugin) setup

### Fixed
- Resource leak on deploy task

## [0.0.1]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Plugin settings (project scoped) : add your environments and define script interfaces
- Action: generate Script JSON body and copy it to clipboard (useful for your postman collections)
- Task: automatically upload scripts to your preferred instance