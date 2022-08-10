describe('My First Test', () => {
  it('does not pass accessibility check', () => {
    cy.visit('/');
    cy.injectAxe();
    cy.checkA11y();
  });
});
