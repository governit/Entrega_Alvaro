package coco;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mwe.utils.StandaloneSetup;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

import edu.test7.Test7StandaloneSetup;
import edu.coco.afm2coco.Afm2CoCoStandaloneSetup;

//https://www.eclipse.org/epsilon/doc/articles/in-memory-emf-model/
public class XtextModelManager {
	
	private String path;
	private String uri;
	
	public XtextModelManager(String path){
		this.path = path;
		this.uri = "platform:/resource/CoCoStandalone/" + path;
	}
	
	public void loadXtextModelAsEcoreAfm2CoCo() {
		Resource resource = setStandaloneXtextSetupAfm2CoCo();
		
		InMemoryEmfModel emfModel = new InMemoryEmfModel(resource);
		emfModel.loadModel();
		emfModel.setModelFile(this.path);
		emfModel.setName("FMFaMa");
		emfModel.setReadOnLoad(true);
		emfModel.setStoredOnDisposal(false);
		emfModel.setMetamodelUri("http://www.coco.edu/afm2coco/Afm2CoCo");
	}
	
	public void loadXtextModelAsEcoreCoCo() {
		Resource resource = setStandaloneXtextSetupCoCo();
		
		InMemoryEmfModel emfModel = new InMemoryEmfModel(resource);
		emfModel.loadModel();
		emfModel.setModelFile(this.path);
		emfModel.setName("DSLCoCo");
		emfModel.setReadOnLoad(true);
		emfModel.setStoredOnDisposal(false);
		emfModel.setMetamodelUri("http://www.test7.edu/Test7");
	}
	
	private Resource setStandaloneXtextSetupAfm2CoCo(){
		System.out.println("OPCION1111.666...");
		System.out.println("OPCION1111...11."+XtextResource.OPTION_RESOLVE_ALL);
		System.out.println("OPCION11222f..hh..");
		StandaloneSetup setup = new StandaloneSetup();
		setup.setPlatformUri("../");
		Injector injector = new Afm2CoCoStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet =  injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI(this.uri);
		Resource resource = resourceSet.getResource(uri, true);
		
		return resource;
	}
	
	private Resource setStandaloneXtextSetupCoCo(){
		System.out.println("OPCION1111....");
		System.out.println("OPCION1111...11."+XtextResource.OPTION_RESOLVE_ALL);
		System.out.println("OPCION11222f....");

		StandaloneSetup setup = new StandaloneSetup();
		setup.setPlatformUri("../");
		Injector injector = new Test7StandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet =  injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI(this.uri);
		Resource resource = resourceSet.getResource(uri, true);
		
		return resource;
	}
}
