describe('Mail', () => {
  it('start page', () => {
    cy.visit(`http://${Cypress.env('mailDevServer')}/#/`);
    cy.contains('MailDev');
    cy.contains('CIRCABC Share notification');
    cy.title().should('eq', 'MailDev (+2)');
    // cy.injectAxe();
    // cy.checkA11y();
  });
});
