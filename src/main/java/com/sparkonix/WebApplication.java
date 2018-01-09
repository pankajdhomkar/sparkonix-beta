package com.sparkonix;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.sparkonix.api.CreateAdminUser;
import com.sparkonix.auth.WebAuthenticator;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.ComplaintDetailDAO;
import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.CustomerManufacturerIndexDAO;
import com.sparkonix.dao.CustomerResellerIndexDAO;
import com.sparkonix.dao.IssueNumberDetailDAO;
import com.sparkonix.dao.IssueTechnicianDAO;
import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.MachineDocumentDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.dao.UserRoleDAO;
import com.sparkonix.dao.UserRoleIndexDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.ComplaintDetail;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.IssueNumberDetail;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.MachineAmcHistory;
import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.MachineDocument;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.TechManufacturer;
import com.sparkonix.entity.TechReseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.UserRole;
import com.sparkonix.entity.UserRoleIndex;
import com.sparkonix.resources.BroadcastMessagesResource;
import com.sparkonix.resources.CompanyLocationResource;
import com.sparkonix.resources.CompanyLocationsResource;
import com.sparkonix.resources.ComplaintDetailResource;
import com.sparkonix.resources.ComplaintDetailsResource;
import com.sparkonix.resources.CustomerDetailResource;
import com.sparkonix.resources.CustomerDetailsResource;
import com.sparkonix.resources.LoginResource;
import com.sparkonix.resources.MachineAmcServiceHistoriesResource;
import com.sparkonix.resources.MachineAmcServiceHistoryResource;
import com.sparkonix.resources.MachineDocumentResource;
import com.sparkonix.resources.MachineDocumentsResource;
import com.sparkonix.resources.MachineResource;
import com.sparkonix.resources.MachinesResource;
import com.sparkonix.resources.ManufacturerDetailResource;
import com.sparkonix.resources.ManufacturerDetailsResource;
import com.sparkonix.resources.PhoneOperatorResource;
import com.sparkonix.resources.PhoneOperatorsResource;
import com.sparkonix.resources.PrivacyPolicyResource;
import com.sparkonix.resources.QRCodeResource;
import com.sparkonix.resources.QRCodesResource;
import com.sparkonix.resources.ResellerDeatilsResource;
import com.sparkonix.resources.ResellerDetailResource;
import com.sparkonix.resources.TermsConditionResource;
import com.sparkonix.resources.UserResource;
import com.sparkonix.resources.UsersResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WebApplication extends Application<WebConfiguration> {
	public static void main(String[] args) throws Exception {
		new WebApplication().run(args);
	}

	private final HibernateBundle<WebConfiguration> hibernateBundle = new HibernateBundle<WebConfiguration>(
		CompanyLocation.class, ComplaintDetail.class, IssueNumberDetail.class, PhoneOperator.class, Customer.class,
		Machine.class, MachineAmcHistory.class, MachineAmcServiceHistory.class, MachineDocument.class, Manufacturer.class,
		QRCode.class, Reseller.class, TechManufacturer.class, TechReseller.class, User.class, UserRole.class, UserRoleIndex.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(WebConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};

	@Override
	public void initialize(Bootstrap<WebConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
				bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

		bootstrap.addBundle(new AssetsBundle("/webapp", "/", "index.html", "WebApp"));
		bootstrap.addBundle(new AssetsBundle());
		bootstrap.addBundle(new MigrationsBundle<WebConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(WebConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(WebConfiguration configuration, Environment environment) {
		ApplicationContext.init(configuration);
		((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

final CompanyLocationDAO companyLocationDAO = new CompanyLocationDAO(hibernateBundle.getSessionFactory());
		
		final ComplaintDetailDAO complaintDetailDAO = new ComplaintDetailDAO(hibernateBundle.getSessionFactory());
		
		final CustomerDAO customerDAO = new CustomerDAO(hibernateBundle.getSessionFactory());
		
		final CustomerManufacturerIndexDAO customerManufacturerIndexDAO = new CustomerManufacturerIndexDAO(hibernateBundle.getSessionFactory());
		
		final CustomerResellerIndexDAO customerResellerIndexDAO = new CustomerResellerIndexDAO(hibernateBundle.getSessionFactory());
		
		final IssueNumberDetailDAO issueNumberDetailDAO = new IssueNumberDetailDAO(hibernateBundle.getSessionFactory());
		
		final IssueTechnicianDAO issueTechnicianDAO = new IssueTechnicianDAO(hibernateBundle.getSessionFactory());
		
//		final MachineAmcHistoryDAO machineAmcHistoryDAO = new MachineAmcHistoryDAO(hibernateBundle.getSessionFactory());
		
		final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO = new MachineAmcServiceHistoryDAO(hibernateBundle.getSessionFactory());
		
		final MachineDAO machineDAO = new MachineDAO(hibernateBundle.getSessionFactory());
		
		final MachineDocumentDAO machineDocumentDAO = new MachineDocumentDAO(hibernateBundle.getSessionFactory());
		
		final ManufacturerDAO manufacturerDAO = new ManufacturerDAO(hibernateBundle.getSessionFactory());
		
		final PhoneOperatorDAO phoneOperatorDAO = new PhoneOperatorDAO(hibernateBundle.getSessionFactory());
		
		final QRCodeDAO qrCodeDAO = new QRCodeDAO(hibernateBundle.getSessionFactory());
		
		final ResellerDAO resellerDAO = new ResellerDAO(hibernateBundle.getSessionFactory());
		
//		final TechManufacturerDAO techManufacturerDAO = new TechManufacturerDAO(hibernateBundle.getSessionFactory());
//		
//		final TechResellerDAO techResellerDAO = new TechResellerDAO(hibernateBundle.getSessionFactory());
		
		final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
		
		final UserRoleDAO userRoleDAO = new UserRoleDAO(hibernateBundle.getSessionFactory());
		
		final UserRoleIndexDAO userRoleIndexDAO = new UserRoleIndexDAO(hibernateBundle.getSessionFactory());
		
		// for uploading multipart/form-data
		environment.jersey().register(MultiPartFeature.class);

		CachingAuthenticator<BasicCredentials, User> cachingAuthenticator = new CachingAuthenticator<>(
			environment.metrics(), new WebAuthenticator(hibernateBundle.getSessionFactory()),
			configuration.getAuthenticationCachePolicy());
		System.out.println("CHecking caching authenticatior-----"+configuration.getAuthenticationCachePolicy());
		// register resources
		
		environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
				.setAuthenticator(cachingAuthenticator).setRealm("Sparkonix").buildAuthFilter()));
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
//		environment.jersey().register(RolesAllowedDynamicFeature.class);
		
		/*environment.jersey().register(AuthFactory.binder(
	                new BasicAuthFactory<>(
	                        new GreetingAuthenticator(configuration.getLogin(),
	                                configuration.getPassword()),
	                        "SECURITY REALM",
	                        User.class)));*/

		environment.jersey().register(new CompanyLocationResource(companyLocationDAO));
		environment.jersey().register(new CompanyLocationsResource(companyLocationDAO));

		environment.jersey().register(new ComplaintDetailResource(issueNumberDetailDAO, phoneOperatorDAO, machineDAO, complaintDetailDAO, manufacturerDAO, resellerDAO, customerDAO, issueTechnicianDAO, userDAO, companyLocationDAO));
		environment.jersey().register(new ComplaintDetailsResource(issueNumberDetailDAO, phoneOperatorDAO, machineDAO, complaintDetailDAO, manufacturerDAO, resellerDAO, customerDAO, userDAO, issueTechnicianDAO, companyLocationDAO));

		environment.jersey().register(new CustomerDetailResource(customerDAO, userDAO, machineDAO, companyLocationDAO));
		environment.jersey().register(new CustomerDetailsResource(customerDAO, userDAO, companyLocationDAO, phoneOperatorDAO, manufacturerDAO, resellerDAO, machineDAO, customerResellerIndexDAO, customerManufacturerIndexDAO));

		environment.jersey().register(new MachineAmcServiceHistoryResource(machineAmcServiceHistoryDAO, machineDAO, userDAO));
		environment.jersey().register(new MachineAmcServiceHistoriesResource(machineAmcServiceHistoryDAO, machineDAO, userDAO));

		environment.jersey().register(new MachineResource(machineDAO, qrCodeDAO, manufacturerDAO, resellerDAO, customerDAO, phoneOperatorDAO, userDAO));
		environment.jersey().register(new MachinesResource(machineDAO, qrCodeDAO, complaintDetailDAO, manufacturerDAO, resellerDAO, customerDAO, companyLocationDAO));


		environment.jersey()
		.register(new MachineDocumentResource(machineDocumentDAO, configuration.getMachineDocsDirectory()));
		environment.jersey().register(new MachineDocumentsResource(machineDocumentDAO, manufacturerDAO, configuration.getMachineDocsDirectory()));

		environment.jersey().register(new ManufacturerDetailResource(userDAO, manufacturerDAO, resellerDAO));
		environment.jersey().register(new ManufacturerDetailsResource(manufacturerDAO, companyLocationDAO, machineDAO, phoneOperatorDAO, userDAO, resellerDAO, userRoleIndexDAO));
		
		environment.jersey().register(new PhoneOperatorResource(phoneOperatorDAO));
		environment.jersey().register(new PhoneOperatorsResource(phoneOperatorDAO));
		
		environment.jersey().register(new QRCodeResource(qrCodeDAO, machineDAO));
		environment.jersey().register(new QRCodesResource(qrCodeDAO, userDAO));
		
		environment.jersey().register(new ResellerDetailResource(userDAO, resellerDAO));
		environment.jersey().register(new ResellerDeatilsResource(userDAO, resellerDAO));
		
		environment.jersey().register(new UserResource(userDAO, userRoleIndexDAO, userRoleDAO));
		environment.jersey().register(new UsersResource(userDAO, userRoleDAO, userRoleIndexDAO));

		environment.jersey().register(new LoginResource(userDAO));
		
		// add Super Admin if not exist(yml)
		CreateAdminUser createAdminUser = new CreateAdminUser(userDAO, hibernateBundle.getSessionFactory());
		createAdminUser.createSuperAdminUser();

		environment.jersey().register(new BroadcastMessagesResource(phoneOperatorDAO));
		
		environment.jersey().register(new PrivacyPolicyResource());
		
		environment.jersey().register(new TermsConditionResource());
	}
}
