package control;

import boundary.Account.AccountResource;
import entity.Account;
import provider.AuthenticatedAccount;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@RequestScoped
public class AccountManagement {

    @Produces
    @RequestScoped
    @AuthenticatedAccount
    private Account account;

    @Inject
    AccountResource accountResource;

    public void handleAuthenticationEvent(@Observes @AuthenticatedAccount String email) {
        this.account = accountResource.findByEmail(email);
    }

}