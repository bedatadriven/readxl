package org.renjin.cran.readxl;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.renjin.eval.EvalException;
import org.renjin.primitives.Native;
import org.renjin.primitives.packaging.DllInfo;
import org.renjin.primitives.packaging.DllSymbol;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.StringVector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

public class readxl {

  public static void R_init_readxl(DllInfo dll) {

    // Register all methods in this class
    for (Method method : readxl.class.getMethods()) {
      if(method.getName().startsWith("_readxl_")) {
        try {
          final String methodName = method.getName();
          final MethodHandle methodHandle = MethodHandles.publicLookup().unreflect(method);
          final DllSymbol symbol = new DllSymbol(methodName, methodHandle, DllSymbol.Convention.CALL, true);
          dll.register(symbol);
        } catch (IllegalAccessException e) {
          throw new EvalException("Cannot access method '%s': %s", method.getName(), e.getMessage(), e);
        }
      }
    }
  }

  private static InputStream openInput(SEXP pathSexp) throws FileSystemException {
    if(!(pathSexp instanceof StringVector) && pathSexp.length() == 1) {
      throw new EvalException("'path' argument must a string of length 1");
    }
    String path = pathSexp.asString();

    FileObject fileObject = Native.currentContext().resolveFile(path);
    if(!fileObject.exists()) {
      throw new EvalException(fileObject + " does not exist.");
    }
    return fileObject.getContent().getInputStream();
  }

  public static SEXP _readxl_xlsx_sheets(SEXP pathSEXP) throws IOException {
    try (ReadableWorkbook wb = new ReadableWorkbook(openInput(pathSEXP))) {
      return new StringArrayVector(wb.getSheets().map(s -> s.getName()).collect(Collectors.toList()));
    }
  }

  public static SEXP _readxl_read_xlsx_(SEXP pathSEXP,
                                        SEXP sheet_iSEXP,
                                        SEXP limitsSEXP,
                                        SEXP shimSEXP,
                                        SEXP col_namesSEXP,
                                        SEXP col_typesSEXP,
                                        SEXP naSEXP,
                                        SEXP trim_wsSEXP,
                                        SEXP guess_maxSEXP) {
    throw new UnsupportedOperationException("TODO");
  }


//  _readxl_xls_sheets", (DL_FUNC) &_readxl_xls_sheets, 1},
//  {"_readxl_xls_date_formats", (DL_FUNC) &_readxl_xls_date_formats, 1},
//  {"_readxl_read_xls_", (DL_FUNC) &_readxl_read_xls_, 9},
//  {"_readxl_xlsx_sheets", (DL_FUNC) &_readxl_xlsx_sheets, 1},
//  {"_readxl_xlsx_strings", (DL_FUNC) &_readxl_xlsx_strings, 1},
//  {"_readxl_xlsx_date_formats", (DL_FUNC) &_readxl_xlsx_date_formats, 1},
//  {"_readxl_parse_ref", (DL_FUNC) &_readxl_parse_ref, 1},
//  {"_readxl_read_xlsx_", (DL_FUNC) &_readxl_read_xlsx_, 9},
//  {"_readxl_zip_xml",
}
