describe('My First Test', () => {
  it('EULogin', () => {
    cy.visit('https://localhost:7002/cas');
    cy.get('#username').type('bournja');
    cy.contains('Next').click();
    cy.get('#password').type('Admin123');
    cy.get('.btn').click();
    cy.contains('Successful login');
    
  });
});
