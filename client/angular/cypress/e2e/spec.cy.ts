describe('My First Test', () => {
  it('Visits the initial project page', () => {
    cy.visit('/')
    cy.contains('CIRCABC Share')
    cy.contains('Login with EULogin')
  })

  it('does not pass accessibility check', () => {
    cy.visit('/')
    cy.injectAxe()
    cy.checkA11y()
  })
})
