SET SQL_SAFE_UPDATES = 0;

delete FROM careconnect.DocumentReferenceAuthor where DOCUMENT_REFERENCE_ID;
delete FROM careconnect.DocumentReferenceAttachment where DOCUMENT_REFERENCE_ID;
delete FROM careconnect.DocumentReferenceIdentifier where DOCUMENT_REFERENCE_ID;
delete FROM careconnect.DocumentReference where DOCUMENT_REFERENCE_ID;