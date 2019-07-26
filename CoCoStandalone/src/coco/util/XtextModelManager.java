package coco.util;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mwe.utils.StandaloneSetup;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
//import org.xtext.example.mydsl.MyDslStandaloneSetup;

import com.google.inject.Injector;

import edu.test7.Test7StandaloneSetup;
//import edu.xtext.coco.splot2coco.SPLOT2CoCoStandaloneSetup;
import edu.coco.afm2coco.Afm2CoCoStandaloneSetup;

//https://www.eclipse.org/epsilon/doc/articles/in-memory-emf-model/
public class XtextModelManager {
	
	private String path;
	private String uri;
	public static void main(String x[]) {
		
	}
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
	public void loadXtextModelAsEcoresplot2CoCo() {
		//Resource resource = setStandaloneXtextSetupSPLOT2CoCo();
		
		/*InMemoryEmfModel emfModel = new InMemoryEmfModel(resource);
		emfModel.loadModel();
		emfModel.setModelFile(this.path);
		emfModel.setName("sPLOT2CoCo");
		emfModel.setReadOnLoad(true);
		emfModel.setStoredOnDisposal(false);
		emfModel.setMetamodelUri("http://www.xtext.org/example/mydsl/MyDsl");*/
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

		StandaloneSetup setup = new StandaloneSetup();
		setup.setPlatformUri("../"); 
		Injector injector = new Afm2CoCoStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet =  injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createFileURI(new File(path).getAbsolutePath());
		Resource resource = resourceSet.getResource(uri, true);
		
		return resource;
	}

	
	private Resource setStandaloneXtextSetupCoCo(){
		StandaloneSetup setup = new StandaloneSetup();
		setup.setPlatformUri("../");
		Injector injector = new Test7StandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet =  injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createFileURI(new File(path).getAbsolutePath());
		Resource resource = resourceSet.getResource(uri, true);
		
		return resource;
	}
}
