/*
 * Copyright (C) 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gapid.views;

import static com.google.gapid.perfetto.views.KeyboardMouseHelpDialog.showHelp;
import static com.google.gapid.util.Loadable.MessageType.Error;
import static com.google.gapid.widgets.Widgets.createButtonWithImage;
import static com.google.gapid.widgets.Widgets.createLabel;
import static com.google.gapid.widgets.Widgets.createToggleToolItem;
import static com.google.gapid.widgets.Widgets.exclusiveSelection;
import static com.google.gapid.widgets.Widgets.withLayoutData;
import static java.util.Arrays.stream;

import com.google.common.base.Function;
import com.google.gapid.models.Analytics;
import com.google.gapid.models.Capture;
import com.google.gapid.models.CommandStream;
import com.google.gapid.models.Models;
import com.google.gapid.models.Profile;
import com.google.gapid.models.Profile.Duration;
import com.google.gapid.perfetto.views.RootPanel;
import com.google.gapid.perfetto.views.RootPanel.MouseMode;
import com.google.gapid.perfetto.views.StyleConstants.Colors;
import com.google.gapid.proto.service.Service;
import com.google.gapid.util.Loadable;
import com.google.gapid.util.Messages;
import com.google.gapid.widgets.LoadablePanel;
import com.google.gapid.widgets.Theme;
import com.google.gapid.widgets.Widgets;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

public class PerformanceView extends Composite
    implements Tab, Capture.Listener, CommandStream.Listener, Profile.Listener {
  protected static final Logger LOG = Logger.getLogger(PerformanceView.class.getName());

  private final Models models;
  private  LoadablePanel<PerfTree> loading;
  protected  PerfTree tree;

  public PerformanceView(Composite parent, Models models, Widgets widgets) {
    super(parent, SWT.NONE);
    this.models = models;

    setLayout(new GridLayout(1, false));
    TopBar topBar = withLayoutData(new TopBar(this, widgets.theme),
        new GridData(SWT.FILL, SWT.TOP, true, false));
    Consumer<MouseMode> modeSelector = topBar.buildModeActions(widgets.theme, m -> {
      // loading = LoadablePanel.create(this, widgets, p -> new PerfTree(p, models, widgets));
      // tree = loading.getContents();
      // tree.dispose();
      // loading.dispose();
      // loading = LoadablePanel.create(this, widgets, p -> new PerfTree(p, models, widgets));
      // tree = loading.getContents();
      // tree.addColumn("2333", s-> "23");
      // loading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      // this.redraw();
      // this.requestLayout();

      double addition = Math.random();
      switch (m) {
        case All:
          tree.addColumn("Texture fetch stall", $ -> String.format("%.3f", 6 * (0.5+1*addition)));
          tree.addColumn("Texture L1 miss rate", $ -> String.format("%.3f", 60 * (0.5+1*addition)));
          tree.addColumn("GPU utilization", $ -> String.format("%.3f", 40 * (0.8+0.4*addition)));
          tree.addColumn("GPU bus busy percentage", $ -> String.format("%.3f", 30 * (0.5+1*addition)));
          tree.addColumn("Fragment EFU instructions/second", $ -> String.format("%.3f", 1000000 * (0.5+1*addition)));
          tree.addColumn("Texture/vertex", $ -> String.format("%.3f", 0.05 * (0.5+1*addition)));
          tree.addColumn("GPU frequency", $ -> String.format("%.3f", 240000000.0 * (0.8+0.4*addition)));
          tree.packColumn();
          tree.refresh();
          break;
        case GPU:
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          tree.addColumn("GPU bus busy percentage", $ -> String.format("%.3f", 30 * (0.5+1*addition)));
          tree.addColumn("GPU utilization", $ -> String.format("%.3f", 40 * (0.8+0.4*addition)));
          tree.addColumn("GPU frequency", $ -> String.format("%.3f", 240000000.0 * (0.8+0.4*addition)));
          tree.packColumn();
          tree.refresh();
          break;
        case Texture:
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          tree.addColumn("Texture/vertex", $ -> String.format("%.3f", 0.05 * (0.5+1*addition)));
          tree.addColumn("Texture/vertex", $ -> String.format("%.3f", 0.05 * (0.5+1*addition)));
          tree.addColumn("Texture L1 miss rate", $ -> String.format("%.3f", 60 * (0.5+1*addition)));
          tree.addColumn("Texture fetch stall", $ -> String.format("%.3f", 6 * (0.5+1*addition)));
          tree.packColumn();
          tree.refresh();
          break;
        case Fragment:
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          tree.addColumn("Fragment EFU instructions/second", $ -> String.format("%.3f", 1000000 * (0.5+1*addition)));
          tree.addColumn("Fragment ALU instructions/second", $ -> String.format("%.3f", 4000000 * (0.5+1*addition)));
          tree.addColumn("Fragment instructions/second", $ -> String.format("%.3f", 60000000 * (0.5+1*addition)));
          tree.packColumn();
          tree.refresh();
          break;
        case Favorite:
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          // tree.addColumn("", $ -> "");
          tree.addColumn("GPU utilization", $ -> String.format("%.3f", 40 * (0.8+0.4*addition)));
          tree.addColumn("GPU frequency", $ -> String.format("%.3f", 240000000.0 * (0.8+0.4*addition)));
          tree.addColumn("Texture/vertex", $ -> String.format("%.3f", 0.05 * (0.5+1*addition)));
          tree.addColumn("Texture L1 miss rate", $ -> String.format("%.3f", 60 * (0.5+1*addition)));
          tree.addColumn("Texture fetch stall", $ -> String.format("%.3f", 6 * (0.5+1*addition)));
          tree.addColumn("GPU utilization", $ -> String.format("%.3f", 40 * (0.8+0.4*addition)));
          tree.packColumn();
          tree.refresh();
          break;
      }

      for (int j = 1; j < tree.getTree().getTree().getColumnCount(); j++) {
        double min = Double.MAX_VALUE, max = 0;
        if (tree.getTree().getTree().getItem(0).getText(j).equals("")) {
          continue;
        }
        for (int i = 0; i < tree.getTree().getTree().getItemCount(); i++) {
          double v = Double.parseDouble(tree.getTree().getTree().getItem(i).getText(j));
          min = Math.min(min, v);
          max = Math.max(max, v);
          // System.out.println(tree.getTree().getTree().getItem(i).getText(0));
        }
        double mid = (min + max) / 2;

        for (int i = 0; i < tree.getTree().getTree().getItemCount(); i++) {
          double v = Double.parseDouble(tree.getTree().getTree().getItem(i).getText(j));
          if (v > mid) {
            tree.getTree().getTree().getItem(i).setText(j, "widgets.theme.memoryFirstLevelBackground()");
          }
        }
      }

      tree.getTree().getTree().getItem(1).setBackground(1, widgets.theme.memoryFirstLevelBackground());

      // for (TreeItem item : tree.getTree().getTree().getItems()) {
      //   // Give visual hint to the elements of level 1.
      //   item.setBackground(widgets.theme.memoryFirstLevelBackground());
      // }
      // tree.refresh();

    });
    // modeSelector.accept(MouseMode.All);


    loading = LoadablePanel.create(this, widgets, p -> new PerfTree(p, models, widgets));
    tree = loading.getContents();

    loading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    models.capture.addListener(this);
    models.commands.addListener(this);
    models.profile.addListener(this);
    addListener(SWT.Dispose, e -> {
      models.capture.removeListener(this);
      models.commands.removeListener(this);
      models.profile.removeListener(this);
    });
  }

  @Override
  public Control getControl() {
    return this;
  }

  @Override
  public void reinitialize() {
    updateTree(false);
  }

  @Override
  public void onCaptureLoadingStart(boolean maintainState) {
    updateTree(true);
  }

  @Override
  public void onCaptureLoaded(Loadable.Message error) {
    if (error != null) {
      loading.showMessage(Error, Messages.CAPTURE_LOAD_FAILURE);
    }
  }

  @Override
  public void onCommandsLoaded() {
    updateTree(false);
  }

  @Override
  public void onProfileLoaded(Loadable.Message error) {
    // Create columns for counters after profile get loaded, because we need to know counter numbers.
    for (Service.ProfilingData.Counter counter : models.profile.getData().getCounters()) {
      // tree.addColumnForCounter(counter);
    }
    tree.packColumn();
    tree.refresh();
  }

  private void updateTree(boolean assumeLoading) {
    if (assumeLoading || !models.commands.isLoaded()) {
      loading.startLoading();
      tree.setInput(null);
      return;
    }

    loading.stopLoading();
    tree.setInput(models.commands.getData());
  }

  private static class PerfTree extends CommandTree.Tree {
    private static final int DURATION_WIDTH = 95;

    public PerfTree(Composite parent, Models models, Widgets widgets) {
      super(parent, models, widgets);
      // addColumn("GPU Time", this::formatGpuTime);
      // addColumn("Wall Time", this::formatWallTime);
    }

    @Override
    protected boolean shouldShowImage(CommandStream.Node node) {
      return false;
    }

    public void addColumn(String title, Function<Service.CommandTreeNode, String> formatter) {
      TreeViewerColumn column = addColumn(title, node -> {
        Service.CommandTreeNode data = node.getData();
        if (data == null) {
          // return "";
          return formatter.apply(data);
        } else if (!models.profile.isLoaded()) {
          // return "Profiling...";
          return formatter.apply(data);
        } else {
          return formatter.apply(data);
        }
      }, DURATION_WIDTH);
      column.getColumn().setAlignment(SWT.RIGHT);
    }

    private void addColumnForCounter(Service.ProfilingData.Counter counter) {
      TreeViewerColumn column = addColumn(counter.getName(), node -> {
        Service.CommandTreeNode data = node.getData();
        if (data == null) {
          return "";
        } else if (!models.profile.isLoaded()) {
          return "Profiling...";
        } else {
          Double aggregation = models.profile.getData().getCounterAggregation(data.getCommands(), counter);
          return aggregation.isNaN() ? "" : String.format("%.3f", aggregation);
        }
      }, DURATION_WIDTH);
      column.getColumn().setAlignment(SWT.RIGHT);
    }

    public void refresh() {
      refresher.refresh();
    }

    private String formatGpuTime(Service.CommandTreeNode node) {
      Profile.Duration duration = models.profile.getData().getDuration(node.getCommands());
      return duration == Duration.NONE ? "" : String.format("%.3fms", duration.gpuTime / 1e6);
    }

    private String formatWallTime(Service.CommandTreeNode node) {
      Profile.Duration duration = models.profile.getData().getDuration(node.getCommands());
      return duration == Duration.NONE ? "" : String.format("%.3fms", duration.wallTime / 1e6);
    }
  }

  private static class TopBar extends Composite {
    private final ToolBar toolBar;

    public TopBar(Composite parent, Theme theme) {
      super(parent, SWT.NONE);
      setLayout(new GridLayout(3, false));
      withLayoutData(createLabel(this, "Mode:"),
          new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
      toolBar = withLayoutData(new ToolBar(this, SWT.FLAT | SWT.HORIZONTAL | SWT.TRAIL),
          new GridData(SWT.FILL, SWT.CENTER, true, true));
    }

    public Consumer<MouseMode> buildModeActions(Theme theme, Consumer<MouseMode> onClick) {
      return MouseMode.createToolBar(toolBar, theme, onClick);
    }
  }

  public static enum MouseMode {
    All(" All ", "", Theme::selectionMode),
    GPU(" GPU ", "", Theme::panMode),
    Texture(" Texture ", "", Theme::zoomMode),
    Fragment(" Fragment ", "", Theme::timingMode),
    Favorite(" My Favorite ", "", Theme::timingMode);

    private final String label;
    private final String toolTip;
    private final java.util.function.Function<Theme, Image> icon;

    private MouseMode(String label, String toolTip, java.util.function.Function<Theme, Image> icon) {
      this.label = label;
      this.toolTip = toolTip;
      this.icon = icon;
    }

    private ToolItem createItem(ToolBar bar, Theme theme, Consumer<MouseMode> onClick) {
      ToolItem item = createToggleToolItem(
          bar, null, e -> onClick.accept(this), toolTip);
      item.setText(label);
      return item;
    }

    public static Consumer<MouseMode> createToolBar(
        ToolBar bar, Theme theme, Consumer<MouseMode> onClick) {
      IntConsumer itemSelector = exclusiveSelection(stream(values())
          .map(m -> m.createItem(bar, theme, onClick))
          .toArray(ToolItem[]::new));

      return mode -> {
        onClick.accept(mode);
        itemSelector.accept(mode.ordinal());
      };
    }
  }
}
