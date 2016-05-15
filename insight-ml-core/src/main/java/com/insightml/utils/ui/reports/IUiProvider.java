/**
 * Copyright (c) 2011-2013 Stefan Henss.
 * 
 * @author stefan.henss@gmail.com
 */
package com.insightml.utils.ui.reports;

public interface IUiProvider<I> {

    String getText(final I instances, final int labelIndex);

}
