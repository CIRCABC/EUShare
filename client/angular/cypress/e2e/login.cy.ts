describe('My First Test', () => {
  beforeEach(function () {
    cy.login('chucknorris', 'Qwerty098');
  });

  it('Visits the initial project page', () => {
    cy.contains('Administration').should('not.exist');
  });
});
