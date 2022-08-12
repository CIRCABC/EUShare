describe('Admin login', () => {
  beforeEach(function () {
    cy.login('bournja', 'Admin123');
  });

  it('Visits the initial project page', () => {
    cy.contains('Administration').should('be.visible');
    cy.contains('Jason BOURNE');
    cy.get('.administration > .spanlabel').click();
    // cy.injectAxe();
    // cy.checkA11y();
  });
});
