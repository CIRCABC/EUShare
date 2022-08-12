describe('User Login', () => {
  beforeEach(function () {
    cy.login('chucknorris', 'Qwerty098');
  });

  it('Visits the initial project page', () => {
    cy.contains('My shared files').should('be.visible');
    cy.contains('Shared with me').should('be.visible');
    cy.contains('Administration').should('not.be.exist');
    cy.contains('Chuck NORRIS');
  });
});
