package com.ifc.myelinflow.service.interfaces;

import com.ifc.myelinflow.dto.PopulationSequenceResult;

import java.util.List;

public interface ResultPrinterService {

    void print(List<PopulationSequenceResult> populationSequenceResults);

}
