DROP TABLE IF EXISTS Author;
DROP TABLE IF EXISTS Publication;
CREATE TABLE Author(id integer PRIMARY KEY, forename VARCHAR(255), surname VARCHAR(255));
CREATE TABLE Publication(pureId integer PRIMARY KEY, title VARCHAR(255), outputType VARCHAR(255), publicationStatus VARCHAR(255), acceptedDate VARCHAR(255), publicationDate VARCHAR(255), ePublicationDate VARCHAR(255), journal VARCHAR(255), doi VARCHAR(255), notes VARCHAR(255) );

	