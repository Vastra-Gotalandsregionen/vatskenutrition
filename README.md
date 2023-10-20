### Vätske- och nutritionsbehandling

This application is a digitalization of an earlier printed brochure regarding liquid and nutrition treatment.

## Components

The application consists of two components and depends on an additional component. It consists of:

* The frontend Angular application.
* The backend web application which is deployed in e.g. Apache Tomcat.

Further, it depends on Liferay Portal which needs to run the Skinny JSON plugin.

The frontend application makes requests to the backend web application which, in turn, fetches its data from the Skinny JSON plugin.

