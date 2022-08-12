describe('User Login', () => {
  beforeEach(function () {
    cy.login('chucknorris', 'Qwerty098');
  });

  it('Visits the initial project page', () => {
    cy.contains('Administration').should('not.be.exist');
    cy.contains('Chuck NORRIS');
  });
});
