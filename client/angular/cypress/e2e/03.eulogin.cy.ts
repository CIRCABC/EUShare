describe('EUlogin', () => {
  it('EULogin', () => {
    cy.visit(`https://${Cypress.env('euloginServer')}/cas`);
    cy.get('#username').type('bournja');
    cy.contains('Next').click();
    cy.get('#password').type('Admin123');
    cy.get('.btn').click();
    cy.contains('Successful login');
  });
});
