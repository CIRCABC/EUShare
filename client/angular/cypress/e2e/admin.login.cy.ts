describe('My First Test', () => {
  beforeEach(function () {
    cy.login('bournja', 'Admin123');
  });

  it('Visits the initial project page', () => {
    cy.contains('Administration');
    cy.injectAxe();
    cy.checkA11y();
  });
});
