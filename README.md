[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/grideditorcolumnfix-add-on)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/grideditorcolumnfix-add-on.svg)](https://vaadin.com/directory/component/grideditorcolumnfix-add-on)

# GridEditorColumnFix Add-on for Vaadin 8

GridEditorColumnFix is a an extension add-on for Vaadin 8 Grid fixing two issues with Columns in Grid Editor.

- Editor is out of sync with hidden columns https://github.com/vaadin/framework/issues/8962
- Editor column widths are out of sync with column resizing for https://github.com/vaadin/framework/issues/7276
- Editor not resized when browser window is being resized https://github.com/vaadin/framework/issues/11148

Note, if you use GridFastNavigation add-on, this extension is not required, same fixes are included in it.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/grideditorcolumnfix

## Building and running demo

git clone <url of the GridEditorColumnFix repository>
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for grideditorcolumnfix-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your grideditorcolumnfix-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the grideditorcolumnfix-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/grideditorcolumnfix-demo/ to see the application.

### Debugging client-side

Debugging client side code in the grideditorcolumnfix-demo project:
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application or by adding ?superdevmode to the URL
  - You can access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings.
 
## Release notes

### Version 0.3.0
- Added fix to Editor scroll position not correctly aligned with Grid when opening. See: https://github.com/vaadin/framework/issues/6994

### Version 0.2.1
- Fix: Editor resizing fix did not take Grid's scrollbar into consideration 

### Version 0.2.0
- Added fix to Editor not resized when browser window is being resized bug. See: https://github.com/vaadin/framework/issues/11148

### Version 0.1.0
- Initial release

## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases: Once fixes are found stable, pull request to Framework will be considered

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

GridEditorColumnFix is written by Tatu Lund

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

<...>

For a more comprehensive example, see src/test/java/org/vaadin/template/demo/DemoUI.java

## Features

### Feature A

<...>

### Feature B

<...>

### Feature C

<...>

## API

GridEditorColumnFix JavaDoc is available online at <...>
