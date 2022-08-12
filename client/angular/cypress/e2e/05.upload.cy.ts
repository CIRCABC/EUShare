describe('Upload', () => {
  beforeEach(function () {
    cy.login('bournja', 'Admin123');
  });

  it('Visits the initial project page', () => {
    cy.contains('Administration').should('be.visible');
    cy.contains('Jason BOURNE');

    // cy.injectAxe();
    // cy.checkA11y();
    const fileName = 'files/CIRCABC_Leader_Guide.pdf';
    cy.fixture(fileName, 'binary')
      .then(Cypress.Blob.binaryStringToBlob)
      .then((fileContent) => {
        cy.get('[data-cy="file-input"]').attachFile({
          fileContent,
          filePath: fileName,
          encoding: 'utf-8',
          mimeType: 'application/pdf',
        });
      })
      .then(() => {
        cy.get('[data-cy="submit"]').click({ force: true });
      });
      cy.contains('CIRCABC_Leader_Guide.pdf');
      // cy.injectAxe();
      // cy.checkA11y();
  });

});
