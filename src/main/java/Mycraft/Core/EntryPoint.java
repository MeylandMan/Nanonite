package Mycraft.Core;

import Mycraft.Debug.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;


public class EntryPoint {

    public static void main(String[] args) {

        // Get System specs
        {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();

            // CPU
            CentralProcessor processor = hardware.getProcessor();
            CentralProcessor.ProcessorIdentifier identifier = processor.getProcessorIdentifier();

            // GPU
            GraphicsCard GPU = hardware.getGraphicsCards().getFirst();

            String physicalProcessor = (processor.getLogicalProcessorCount() == processor.getPhysicalProcessorCount())? " " : " " + processor.getPhysicalProcessorCount() + " ";
            Client.processorBrand = "x" + processor.getLogicalProcessorCount() + " " + identifier.getName() + physicalProcessor + "@" + (processor.getMaxFreq() / 1_000_000) + " MHz";
            Client.GPUBrand = GPU.getName() + " (" + ((GPU.getVRam()/1024) / 1024) + " MB)";
        }

        Client.loadModels();
        Logger.log(Logger.Level.INFO, "Creating Window");

        Application application = new Application(640, 480, Client.name);
        application.run();

    }
}