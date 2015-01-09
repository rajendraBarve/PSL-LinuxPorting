package com.ibm.license.app.action;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.ibm.lic.analyze.common.Utility;
import com.ibm.lic.analyze.model.symantec.SymantecComputerModel;
import com.ibm.license.app.common.Constant;
import com.ibm.license.app.exception.DuplicateNameException;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
/**
 * The CreateAccountActionSymantec class uses GenericCreateAction class and
 * create the account for symantec product. It is specific to symantec product.
 * 
 * @version 1.0
 * 
 * */
public class CreateAccountActionSymantec extends GenericCreateAction {
	final static Logger logger =(Logger)LoggerFactory.getLogger(CreateAccountActionSymantec.class); 

	public String createAccount(HttpServletRequest request,
			HttpServletResponse response) throws IllegalAccessException,
			InstantiationException, IOException, ServletException, DuplicateNameException {

		this.request = request;
		this.response = response;

		getCreateAccountFormParameters();
		createAccountId();
		createDataFileFolder();
		loadEntitlementTemplate(Constant.ENTITLEMENT_TEMPLATE);
		getLists();

		for (FileItem item : list) {
			String name = item.getFieldName();

			if (name.equalsIgnoreCase(Constant.DATA)) {
				saveUploadFileToDataFolder(item, Constant.DATA, Constant.SCRIPT_DATA_PATH, false, path+"/data");
			}

			if (name.equalsIgnoreCase(Constant.HYPER)) {
				saveUploadFileToDataFolder(item, Constant.HYPER, Constant.VM_MAPPING_DATA_PATH, true, path+"/data/hyper");
			}

		}

		loadScriptdataIntoDatabase();
		loadModel_listIntoDatabase();
		loadSysmantecData();

		return "";
	}
	
	public String updateAccount(HttpServletRequest request,
			HttpServletResponse response) throws IllegalAccessException,
			InstantiationException, IOException, ServletException ,DuplicateNameException {
		   
		    this.request = request;
		    this.response = response;
		
		getCreateAccountFormParameters();
		getAccountId();
		deleteFilesOnly(path + Constant.DATA_FOLDER_PATH);
		getLists();
		for (FileItem item : list) {
			String name = item.getFieldName();

			if (name.equalsIgnoreCase(Constant.DATA)) {
				deleteFilesOnly(path+"/data/script_data");
				saveUploadFileToDataFolder(item, Constant.DATA, Constant.SCRIPT_DATA_PATH, false, path+"/data");
			}

			if (name.equalsIgnoreCase(Constant.HYPER)) {
				deleteFilesOnly(Constant.VM_MAPPING_DATA_PATH);
				saveUploadFileToDataFolder(item, Constant.HYPER, Constant.VM_MAPPING_DATA_PATH, true, path+"/data/hyper");
			}
	
		}
		loadScriptdataIntoDatabase();
		loadModel_listIntoDatabase();
		loadSysmantecData();
		return "";
		
	}

	private void loadSysmantecData() {

		String lic_path = path + Constant.LICENSE_KEY_SYMANTEC;
		// write license key for each host to lic_path folder
		try {
			Utility.writeLicenseFile((HashMap<String, SymantecComputerModel>) model_list, lic_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:",e);
			//e.printStackTrace();
		}
		this.spvumappingservice.updateSPVUMapperList(spvu_mapper);

	}

}// end of class
